package com.yemenptc.bss.coreservice.ivr;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tmf-api/ivr/v5")
@RequiredArgsConstructor
public class IvrController {

    private final IvrService ivrService;

    @PostMapping("/call/start")
    public ResponseEntity<Map<String, Object>> startCall(@RequestBody Map<String, String> request) {
        String callerId = request.get("callerId");
        String calledNumber = request.get("calledNumber");
        
        IvrService.IvrCallSession session = ivrService.startCall(callerId, calledNumber);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", session.sessionId,
            "status", session.state.name(),
            "menu", ivrService.getMainMenu().welcomeMessage
        ));
    }

    @PostMapping("/call/{sessionId}/dtmf")
    public ResponseEntity<Map<String, Object>> processDtmf(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {
        
        String digit = request.get("digit");
        IvrService.IvrCallSession session = ivrService.processDtmf(sessionId, digit);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", session.sessionId,
            "state", session.state.name(),
            "lastAction", session.lastAction != null ? session.lastAction : "",
            "currentMenu", session.currentMenuId
        ));
    }

    @PostMapping("/call/{sessionId}/end")
    public ResponseEntity<Map<String, Object>> endCall(@PathVariable String sessionId) {
        IvrService.IvrCallSession session = ivrService.endCall(sessionId);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "status", "COMPLETED",
            "duration", session != null && session.startTime != null 
                ? (System.currentTimeMillis() - session.startTime.getTime()) / 1000 + "s" 
                : "0s"
        ));
    }

    @GetMapping("/call/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(@PathVariable String sessionId) {
        IvrService.IvrCallSession session = ivrService.getSession(sessionId);
        
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of(
            "sessionId", session.sessionId,
            "callerId", session.callerId,
            "calledNumber", session.calledNumber,
            "state", session.state.name(),
            "currentMenu", session.currentMenuId,
            "lastAction", session.lastAction != null ? session.lastAction : ""
        ));
    }

    @GetMapping("/calls/active")
    public ResponseEntity<List<IvrService.IvrCallSession>> getActiveCalls() {
        return ResponseEntity.ok(ivrService.getActiveSessions());
    }
}