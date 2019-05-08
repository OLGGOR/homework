package ru.sberbank.school.task02.calculators;

import lombok.NonNull;
import ru.sberbank.school.task02.ExtendedFxConversionService;
import ru.sberbank.school.task02.ExternalQuotesService;
import ru.sberbank.school.task02.util.Beneficiary;
import ru.sberbank.school.task02.util.ClientOperation;
import ru.sberbank.school.task02.util.Quote;
import ru.sberbank.school.task02.util.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
    public Optional<BigDecimal> convertReversed(@NonNull ClientOperation operation, @NonNull Symbol symbol,
                                                @NonNull BigDecimal amount, double delta,
                                                @NonNull Beneficiary beneficiary) {

        List<Quote> quotes = getExternalQuotesService().getQuotes(symbol);
        if (quotes == null || quotes.isEmpty()) {
            throw new NullPointerException();
        }

        Set<Quote> exactHit = new HashSet<>();
        Set<Quote> deltaHit = new HashSet<>();
        getListsSuitableQuotes(amount, operation, exactHit, deltaHit, quotes, delta);

        if (exactHit.isEmpty() && deltaHit.isEmpty()) {
            return Optional.empty();
        }

        Set<Quote> tmp = !exactHit.isEmpty() ? exactHit : deltaHit;
        Quote find = tmp.iterator().next();
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

        return Optional.of(getCurrentAmount(BigDecimal.ONE, find, operation));
    }

    private void getListsSuitableQuotes(BigDecimal amount, ClientOperation operation, Set<Quote> exactHit,
                                          Set<Quote> deltaHit, List<Quote> quotes, double delta) {
        BigDecimal curVolume;
        BigDecimal addDelta;
        BigDecimal subDelta;

        for (Quote current : quotes) {
            curVolume = getCurrentAmount(amount, current, operation);
            addDelta = getCurrentAmount(amount.add(BigDecimal.valueOf(delta)), current, operation);
            subDelta = getCurrentAmount(amount.subtract(BigDecimal.valueOf(delta)), current, operation);

            BigDecimal tmp = (current.isInfinity() || curVolume.compareTo(current.getVolumeSize()) < 0)
                    ? curVolume : (addDelta.compareTo(current.getVolumeSize()) < 0) ? addDelta
                    : (subDelta.compareTo(current.getVolumeSize()) < 0) ? subDelta : null;
            if (tmp == null) {
                continue;
            }

            boolean isSuit = true;

            for (Quote other : quotes) {
                if (isSuit(current, other, tmp)) {
                    isSuit = false;
                }
            }

            if (tmp.equals(curVolume) && isSuit) {
                exactHit.add(current);
            } else if (isSuit) {
                deltaHit.add(current);
            }
        }
    }

    private boolean isSuit(Quote current, Quote other, BigDecimal volume) {
        boolean currentGreater = current.isInfinity() || (current.getVolumeSize().compareTo(other.getVolumeSize()) > 0
                && current.getVolumeSize().compareTo(volume) > 0);

        return currentGreater && other.getVolumeSize().compareTo(volume) > 0;
    }

    private BigDecimal getCurrentAmount(BigDecimal a, Quote quote, ClientOperation operation) {
        return (operation == ClientOperation.BUY)
                ? a.divide(quote.getBid(), 10, RoundingMode.HALF_UP)
                : a.divide(quote.getOffer(), 10, RoundingMode.HALF_UP);
    }

}
