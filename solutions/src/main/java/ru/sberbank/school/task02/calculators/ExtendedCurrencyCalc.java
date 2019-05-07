package ru.sberbank.school.task02.calculators;

import ru.sberbank.school.task02.ExtendedFxConversionService;
import ru.sberbank.school.task02.ExternalQuotesService;
import ru.sberbank.school.task02.util.Beneficiary;
import ru.sberbank.school.task02.util.ClientOperation;
import ru.sberbank.school.task02.util.Quote;
import ru.sberbank.school.task02.util.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtendedCurrencyCalc extends CurrencyCalc implements ExtendedFxConversionService {

    public ExtendedCurrencyCalc(ExternalQuotesService externalQuotesService) {
        super(externalQuotesService);
    }

    @Override
    public Optional<BigDecimal> convertReversed(ClientOperation operation, Symbol symbol,
                                                BigDecimal amount, Beneficiary beneficiary) {
        return convertReversed(operation, symbol, amount, 0, beneficiary);
    }

    @Override
    public Optional<BigDecimal> convertReversed(ClientOperation operation, Symbol symbol, BigDecimal amount,
                                                double delta, Beneficiary beneficiary) {
        if (operation == null || symbol == null || amount == null
                || beneficiary == null ) {
            return Optional.empty();
        }

        List<Quote> quotes = getExternalQuotesService().getQuotes(symbol);
        if (quotes == null || quotes.isEmpty()) {
            return Optional.empty();
        }
        sortQuotes(quotes);

        List<Quote> deltaHit = new ArrayList<>();
        List<Quote> exactHit = new ArrayList<>();
        getListsSuitableQuotes(quotes, exactHit, deltaHit, operation, amount, delta);


        if (exactHit.isEmpty() && deltaHit.isEmpty()) {
            return Optional.empty();
        }

        List<Quote> tmp = !exactHit.isEmpty() ? exactHit : deltaHit;
        Quote find = tmp.get(0);
        boolean compareResult;

        for (Quote q : tmp) {
            compareResult = (operation == ClientOperation.BUY)
                    ? q.getBid().compareTo(find.getBid()) > 0
                    : q.getOffer().compareTo(find.getOffer()) < 0;

            if (beneficiary == Beneficiary.BANK && compareResult) {
                find = q;
            } else if (beneficiary == Beneficiary.CLIENT && !compareResult) {
                find = q;
            }
        }

        return Optional.of(operation == ClientOperation.BUY
                ? BigDecimal.ONE.divide(find.getBid(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ONE.divide(find.getOffer(), 2, RoundingMode.HALF_UP));
    }

    private void getListsSuitableQuotes(List<Quote> quotes, List<Quote> exactHit, List<Quote> deltaHit,
                                               ClientOperation operation, BigDecimal amount, double delta) {
        BigDecimal result;
        BigDecimal previousVolume = null;

        for (Quote q : quotes) {
            result = (operation == ClientOperation.BUY)
                    ? amount.divide(q.getBid(), 5, RoundingMode.HALF_UP)
                    : amount.divide(q.getOffer(), 5, RoundingMode.HALF_UP);

            if (isSuit(q, previousVolume, result)) {
                exactHit.add(q);
            } else if (isSuit(q, previousVolume, result.add(BigDecimal.valueOf(delta)))
                    || isSuit(q, previousVolume, result.subtract(BigDecimal.valueOf(delta)))) {
                deltaHit.add(q);
            }
            previousVolume = q.getVolumeSize();
        }
    }

    private boolean isSuit(Quote current, BigDecimal previous, BigDecimal result) {
        return previous != null && result.compareTo(previous) >= 0
                && (current.isInfinity() || result.compareTo(current.getVolumeSize()) < 0);
    }

}
