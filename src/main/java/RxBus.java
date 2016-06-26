import domain.Event;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.util.EventObject;

public class RxBus {

    public static RxBus instance = new RxBus();

    private RxBus(){}

    private final Subject<Object, Object> rxBus = new SerializedSubject<>(PublishSubject.create());

    public void send(String stream, Object object)
    {
        Event event = new Event(stream, object);
        rxBus.onNext(event);
    }

    public interface ReceiveOnComputationThread
    {
        void OnReceive(String stream, Object object);
    }

    public void registerOnComputationThread(final ReceiveOnComputationThread receiveOnComputationThread)
    {
        rxBus.observeOn(Schedulers.computation()).subscribe(
                new Action1<Object>() {
                    @Override
                    public void call(Object object) {
                        Event event = (Event) object;
                        receiveOnComputationThread.OnReceive(event.getStream(), event.getObject());
                    }
                }
        );
    }

}
