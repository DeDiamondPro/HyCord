package kr.syeyoung.stomp;

public interface StompMessageHandler {
    void handle(StompInterface stompInterface, StompPayload stompPayload);
}
