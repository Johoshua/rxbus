import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.Action1;
import rx.functions.Func1;

public class Application {
    public static void main(String[] args) {

        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        httpClient.start();
        ObservableHttp.createRequest(HttpAsyncMethods.createGet("http://demo.howopensource.com/sse/"), httpClient)
                .toObservable()
                .flatMap(new Func1<ObservableHttpResponse, Observable<?>>() {
                    @Override
                    public Observable<?> call(ObservableHttpResponse observableHttpResponse) {
                        return observableHttpResponse.getContent().map(new Func1<byte[], Object>() {

                            @Override
                            public Object call(byte[] bytes) {
                                return new String(bytes);
                            }
                        });
                    }
                })
                .toBlocking()
                .forEach(new Action1<Object>() {

                    @Override
                    public void call(Object o) {
                        System.out.println("print stream starts");
                        System.out.println(o);
                    }
                });
    }


}
