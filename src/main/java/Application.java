import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {


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
                //System.out.println(s);
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


        Observable.create(new Observable.OnSubscribe<String>() {
                              @Override
                              public void call(Subscriber<? super String> subscriber) {

                                  Thread thread = new Thread(new Runnable()
                                  {
                                      @Override
                                      public void run() {
                                          for(int i=0; i<10; i++)
                                          {
                                              if(!subscriber.isUnsubscribed())
                                                  subscriber.onNext("pushed value " + Integer.toString(i));
                                          }
                                      }
                                  });
                                  thread.start();
                              }
                          }
        ).subscribeOn(new Scheduler() {
            @Override
            public Worker createWorker() {
                System.out.println("do some work");
                return null;
            }
        });

        RxBus.instance.registerOnComputationThread(new RxBus.ReceiveOnComputationThread(){

            @Override
            public void OnReceive(String stream, Object object) {
                if(stream.equals("feed"))
                    System.out.println("just got new feed " + object);
            }
        });

        RxBus.instance.send("feed", "a");
        RxBus.instance.send("feed", "b");
        RxBus.instance.send("fee", "c");
        RxBus.instance.send("", "d");
    }


}
