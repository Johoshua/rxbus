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

        RxBus.instance.registerOnComputationThread(new RxBus.ReceiveOnComputationThread(){

            @Override
            public void OnReceive(String stream, Object object) {
                for(int i=0; i<Integer.MAX_VALUE; i++);
                if(stream.equals("feed"))
                    System.out.println("just got new feed " + object);
            }
        });

        for(int i=0; i<10000000; i++)
        {
            RxBus.instance.send("feed", Integer.toString(i));
        }

    }


}
