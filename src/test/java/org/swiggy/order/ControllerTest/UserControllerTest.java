package org.swiggy.order.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.swiggy.order.DTO.UserRequest;
import org.swiggy.order.Model.User;
import org.swiggy.order.Service.UserService.UserService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String REGISTER_USER = "/users";

    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @Test
    public void testRegisterUserThrowsBadRequestForEmptyPassword() throws Exception {

        mockMvc.perform(post(REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"user1\",\"password\":\"\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterUserThrowsBadRequestForEmptyUserName() throws Exception {

        mockMvc.perform(post(REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"\",\"password\":\"pass1\"}"))
                .andDo(print()) // ✅ Prints response for debugging
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterUserWithUserNameAndPassword() throws Exception {

        UserRequest userRequest = mock(UserRequest.class);
        when(userService.createUser(userRequest))
                .thenReturn(new User("user1", "pass1"));
        mockMvc.perform(post(REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"user1\",\"password\":\"pass1\"}"))
                .andDo(print()) // ✅ Prints response for debugging
                .andExpect(status().isOk());
    }

}
