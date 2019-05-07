package ru.sberbank.school.task02;

import ru.sberbank.school.task02.util.Beneficiary;
import ru.sberbank.school.task02.util.FxRequest;
import ru.sberbank.school.task02.util.FxResponse;

import java.util.List;
import java.util.Map;

public class Client implements FxClientController {

    public static void main(String[] args) {
        String benef = System.getenv().get("SBRF_BENEFICIARY");
        Beneficiary beneficiary = Beneficiary.valueOf(benef.toUpperCase());

        System.out.println(beneficiary);

    }

    @Override
    public List<FxResponse> fetchResult(List<FxRequest> requests) {
        return null;
    }

    @Override
    public FxResponse fetchResult(FxRequest requests) {
        return null;
    }
}
