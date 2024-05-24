package org.example.datapoint;

import jakarta.annotation.PostConstruct;
import org.example.annotation.SendItemType;
import org.example.exception.InvalidDptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: SendItemFactory
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:17
 **/
@Component
public class SendItemFactory {

    @Autowired
    private ApplicationContext applicationContext;

    private static Map<String, SendDpValue> map;

    @PostConstruct
    public void init() {
        map = applicationContext.getBeansWithAnnotation(SendItemType.class).values().stream()
                .collect(Collectors.toMap(
                        bean -> bean.getClass().getAnnotation(SendItemType.class).value(),
                        bean -> (SendDpValue) bean));
    }

    public static SendDpValue getSendDpValue(String model) {
        SendDpValue client = map.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(model))
                .map(Map.Entry::getValue)
                .findAny().orElse(null);

        if (Objects.isNull(client)) {
            throw new InvalidDptException("invalid send dpValueItem model: " + model);
        }
        return client;
    }

}
