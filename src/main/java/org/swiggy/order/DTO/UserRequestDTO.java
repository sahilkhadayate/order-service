package org.swiggy.order.DTO;

import lombok.Data;

@Data
public class UserRequestDTO {

    private String password;
    private String username;

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }
}
