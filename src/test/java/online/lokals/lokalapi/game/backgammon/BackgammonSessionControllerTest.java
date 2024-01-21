package online.lokals.lokalapi.game.backgammon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BackgammonSessionControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void testCreateSession_Success() throws Exception {
//        // Mock kullanıcı ve istek detayları
//        String requestBody = "{\"opponent\":\"opponentUsername\", \"settings\":{...}}";
//        String expectedResponse = "{\"sessionId\":\"12345\", \"status\":\"CREATED\", ...}";
//
//        mockMvc.perform(post("/tavla")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(content().json(expectedResponse));
//    }
}