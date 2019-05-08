package ru.sberbank.school.task02;

import ru.sberbank.school.task02.util.ExternalQuotesServiceDemo;
import ru.sberbank.school.task02.util.FxRequest;
import ru.sberbank.school.task02.util.FxResponse;

public class TestClient {

    public static void main(String[] args) {
        if (args.length != 3) {
            return;
        }
        ExternalQuotesService service = new ExternalQuotesServiceDemo();
        Client client = new Client(service);

        FxResponse response = client.fetchResult(new FxRequest(args[0], args[1], args[2]));
        if (!response.isNotFound()) {
            System.out.println(response.toString());
        }
    }

}
