package ru.netology.brokerevents.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionEvent implements Serializable {
    private Long id;
    private Boolean decision;
}