import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import rx.Observable;
import rx.Subscriber;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

public class TestApplication {
    public void test()
    {
        List<String> names = new ArrayList<String>();
        names.add("test1");
        names.add("test2");
        names.add("test3");
        names.add("test4");
        Observable.from(names).subscribe(new Action1<String>() {

            @Override
            public void call(String s) {
                System.out.println("hello " + s + ".\n");
            }

        });

        Observable.create(new Observable.OnSubscribe<String>() {
                              @Override
                              public void call(Subscriber<? super String> subscriber) {
                                  for(int i=0; i<10; i++)
                                  {
                                      if(!subscriber.isUnsubscribed())
                                          subscriber.onNext("pushed value " + Integer.toString(i));
                                  }
                              }
                          }
        ).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });

        Observable.create(new Observable.OnSubscribe<String>() {
                              @Override
                              public void call(Subscriber<? super String> subscriber) {
                                  final Subscriber mySubscriber = subscriber;
                                  Thread thread = new Thread(new Runnable()
                                  {
                                      @Override
                                      public void run() {
                                          for(int i=0; i<10; i++)
                                          {
                                              if(!mySubscriber.isUnsubscribed())
                                                  mySubscriber.onNext("pushed value " + Integer.toString(i));
                                          }
                                      }
                                  });
                                  thread.start();
                              }
                          }
        ).subscribe((incomingValue) -> System.out.println(incomingValue),
                (error) -> System.out.println("something is wrong"),
                () -> System.out.println("the observable is finished"));


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
