package cl.io.gateway.example.networkservice;

/**
 * Created by egacl on 31-07-16.
 */
public class TestMessage {

    private String text;

    public TestMessage() {}

    public TestMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestMessage{");
        sb.append("text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
