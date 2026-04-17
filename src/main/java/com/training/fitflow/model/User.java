package com.training.fitflow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    protected Long id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected Boolean active;
}
