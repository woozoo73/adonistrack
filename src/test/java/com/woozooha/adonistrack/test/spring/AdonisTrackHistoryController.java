package com.woozooha.adonistrack.test.spring;

import java.util.List;
import java.util.stream.Collectors;

import com.woozooha.adonistrack.aspect.ProfileAspect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.writer.History;

@RestController
public class AdonisTrackHistoryController {

    @GetMapping("/adonis-track/invocations")
    public List<Invocation.InvocationSummary> getInvocations() {
        History history = ProfileAspect.getConfig().getHistory();

        List<Invocation> invocations = history.getInvocationList();

        return invocations.stream().map(i -> new Invocation.InvocationSummary(i)).collect(Collectors.toList());
    }

    @GetMapping("/adonis-track/invocations/{id}")
    public Invocation getInvocation(@PathVariable String id) {
        History history = ProfileAspect.getConfig().getHistory();

        List<Invocation> invocations = history.getInvocationList();

        return invocations.stream().filter(i -> id.equals(i.getId())).findFirst().orElse(null);
    }

}
