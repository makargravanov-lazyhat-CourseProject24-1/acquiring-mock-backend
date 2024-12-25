package ru.jetlabs.acquiringmockbackend.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UUIDTimedData extends AbstractTimedData {
    private String uuid;
    public UUIDTimedData(String uuid){
        super(LocalDateTime.now().plusMinutes(15));
        this.uuid = uuid;
    }
    public boolean compareByUUID(UUIDTimedData data){
        return data.getUuid().equals(this.uuid);
    }
}
