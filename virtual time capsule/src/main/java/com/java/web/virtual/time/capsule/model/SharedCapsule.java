package com.java.web.virtual.time.capsule.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class SharedCapsule {
    private Capsule capsule;

    private Set<String> usernames;
}
