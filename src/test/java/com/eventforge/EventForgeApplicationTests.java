package com.eventforge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EventForgeApplicationTests {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper json;

 @Test void publicHealthAndProtectedRouteCoverage() throws Exception {
  mvc.perform(get("/health")).andExpect(status().isOk());
  mvc.perform(get("/api/events")).andExpect(status().isUnauthorized());
  String owner=token("owner-"+UUID.randomUUID()+"@test.local");
  mvc.perform(get("/api/events").header("Authorization", bearer(owner))).andExpect(status().isOk());
 }
 @Test void incompleteDependencyCannotBeCompleted() throws Exception {
  String token=token("task-"+UUID.randomUUID()+"@test.local"); String event=event(token, 5, "1000.00");
  String dependency=id(created(token,"/api/events/"+event+"/tasks", "{\"title\":\"Dependency\",\"priority\":\"HIGH\",\"status\":\"TODO\",\"dependencyIds\":[]}"));
  String task=id(created(token,"/api/events/"+event+"/tasks", "{\"title\":\"Blocked\",\"priority\":\"HIGH\",\"status\":\"TODO\",\"dependencyIds\":[\""+dependency+"\"]}"));
  mvc.perform(patch("/api/events/"+event+"/tasks/"+task+"/complete").header("Authorization",bearer(token))).andExpect(status().isConflict());
 }
 @Test void acceptedGuestAtCapacityAndBudgetCapAreRejected() throws Exception {
  String token=token("limits-"+UUID.randomUUID()+"@test.local"); String event=event(token,1,"100.00");
  String first=id(created(token,"/api/events/"+event+"/guests","{\"name\":\"One\",\"email\":\"one"+UUID.randomUUID()+"@test.local\"}"));
  mvc.perform(patch("/api/events/"+event+"/guests/"+first+"/status").header("Authorization",bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"ACCEPTED\"}")).andExpect(status().isOk());
  String second=id(created(token,"/api/events/"+event+"/guests","{\"name\":\"Two\",\"email\":\"two"+UUID.randomUUID()+"@test.local\"}"));
  mvc.perform(patch("/api/events/"+event+"/guests/"+second+"/status").header("Authorization",bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"ACCEPTED\"}")).andExpect(status().isConflict());
  String category=id(created(token,"/api/events/"+event+"/budget-categories","{\"name\":\"Food\",\"plannedAmount\":\"100.00\"}"));
  mvc.perform(post("/api/events/"+event+"/expenses").header("Authorization",bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{\"categoryId\":\""+category+"\",\"description\":\"Too much\",\"amount\":\"101.00\",\"expenseDate\":\"2025-12-01\"}")).andExpect(status().isConflict());
 }
 @Test void overlappingScheduleAndOtherOwnersAreRejected() throws Exception {
  String owner=token("schedule-"+UUID.randomUUID()+"@test.local"); String event=event(owner,5,"1000.00");
  created(owner,"/api/events/"+event+"/schedule-items","{\"title\":\"First\",\"startTime\":\"2025-12-01T10:00:00\",\"endTime\":\"2025-12-01T11:00:00\"}");
  mvc.perform(post("/api/events/"+event+"/schedule-items").header("Authorization",bearer(owner)).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Overlap\",\"startTime\":\"2025-12-01T10:30:00\",\"endTime\":\"2025-12-01T11:30:00\"}")).andExpect(status().isConflict());
  mvc.perform(get("/api/events/"+event).header("Authorization",bearer(token("other-"+UUID.randomUUID()+"@test.local")))).andExpect(status().isNotFound());
 }
 private String token(String email) throws Exception { MvcResult r=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Tester\",\"email\":\""+email+"\",\"password\":\"password123\"}")).andExpect(status().isCreated()).andReturn(); return json.readTree(r.getResponse().getContentAsString()).get("token").asText(); }
 private String event(String token,int capacity,String budget) throws Exception { return id(created(token,"/api/events","{\"name\":\"Event\",\"eventDate\":\"2030-12-31\",\"venue\":\"Hall\",\"capacity\":"+capacity+",\"budgetLimit\":\""+budget+"\",\"status\":\"PLANNED\"}")); }
 private MvcResult created(String token,String path,String body) throws Exception { return mvc.perform(post(path).header("Authorization",bearer(token)).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn(); }
 private String id(MvcResult result) throws Exception { JsonNode n=json.readTree(result.getResponse().getContentAsString()); return n.get("id").asText(); }
 private String bearer(String token) { return "Bearer "+token; }
}
