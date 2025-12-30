package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.entity.ExpenseGroup;
import com.project.splitwiseMirco.service.ExpenseGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class ExpenseGroupController {

    private final ExpenseGroupService service;

    public ExpenseGroupController(ExpenseGroupService service) {
        this.service = service;
    }
    @GetMapping
    public List<ExpenseGroup> getMyGroups() {
        return service.getMyGroups();
    }
    @PostMapping
    public ExpenseGroup create(@RequestParam String name) {
        return service.createGroup(name);
    }

    @PostMapping("/{groupId}/members/{userId}")
    public void addMember(
            @PathVariable String groupId,
            @PathVariable String userId
    ) {
        service.addMember(groupId, userId);
    }
}
