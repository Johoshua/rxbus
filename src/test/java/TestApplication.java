import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

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


        RxBus.instance.registerOnComputationThread(new RxBus.ReceiveOnComputationThread(){

            @Override
            public void OnReceive(String stream, Object object) {
                //for(int i=0; i<Integer.MAX_VALUE; i++);
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
