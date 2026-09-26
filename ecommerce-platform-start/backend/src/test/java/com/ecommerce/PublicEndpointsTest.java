package com.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Endpoint-urile pe care le apeleaza frontend-ul fara token: consola de practica si catalogul. */
@SpringBootTest
@AutoConfigureMockMvc
class PublicEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void practiceEndpointsReturnTextExpectedByFrontend() throws Exception {
        mockMvc.perform(get("/api/practice"))
                .andExpect(status().isOk())
                .andExpect(content().string("api initialised"));

        String body = "{\"name\":\"demo\"}";
        mockMvc.perform(post("/api/practice").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(content().string("youve posted: " + body));
        mockMvc.perform(put("/api/practice").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(content().string("your update is : " + body));
        mockMvc.perform(patch("/api/practice").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(content().string("you have updated the : " + body));
        mockMvc.perform(delete("/api/practice").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(content().string("youve deleted : " + body));
    }

    @Test
    void catalogIsPublicAndFilterableByCategory() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].category").isNotEmpty());

        mockMvc.perform(get("/api/products").param("category", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void configInfoAndHealthArePublic() throws Exception {
        mockMvc.perform(get("/api/config/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile").value("dev"));
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
