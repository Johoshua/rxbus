import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.observables.AsyncOnSubscribe;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainbow on 6/25/16.
 */
public class Application {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String res = restTemplate.getForObject("https://api.github.com/events", String.class);

        System.out.println(res);

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



    }


}
