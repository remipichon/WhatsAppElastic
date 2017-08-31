package co.paan.configuration;

/**
 * Created by remi on 17/10/15.
 */
public enum Channels {

    PARSEFILE("/parseFileFeedback/");

    String name;

    Channels(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
