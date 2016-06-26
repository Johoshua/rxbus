import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    public static RxBus instance = new RxBus();

    private RxBus(){}

    private final Subject<Object, Object> rxBus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o)
    {
        rxBus.onNext(o);
    }

    public Observable<Object> toObservable()
    {
        return rxBus;
    }

}
