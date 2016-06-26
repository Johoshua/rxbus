package domain;

public class Event {

    private String stream;
    private Object object;


    public String getStream() {
        return stream;
    }

    public Object getObject() {
        return object;
    }

    public Event(String stream, Object object)
    {
        this.stream = stream;
        this.object = object;
    }



}
