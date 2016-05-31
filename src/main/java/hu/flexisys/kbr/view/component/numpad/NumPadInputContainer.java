package hu.flexisys.kbr.view.component.numpad;

public interface NumPadInputContainer {

    void onMaxLengthReached(String kod);

    void onInput();

    void onMessage(String message);
}
