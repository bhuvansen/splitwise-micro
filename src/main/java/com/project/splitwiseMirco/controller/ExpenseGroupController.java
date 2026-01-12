package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.AddMemberRequest;
import com.project.splitwiseMirco.dto.CreateGroupRequest;
import com.project.splitwiseMirco.entity.ExpenseGroup;
import com.project.splitwiseMirco.entity.GroupMember;
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
    public ExpenseGroup create(@RequestBody CreateGroupRequest request) {
        return service.createGroup(request.getName());
    }

    @PostMapping("/{groupId}/members/{userId}")
    public void addMember(
            @PathVariable String groupId,
            @PathVariable String userId
    ) {
        service.addMember(groupId, userId);
    }

    @PostMapping("/{groupId}/members")
    public void addMemberByEmail(
            @PathVariable String groupId,
            @RequestBody AddMemberRequest request
    ) {
        service.addMemberByEmail(groupId, request.getEmail());
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable String groupId) {
        System.out.println("Deleting group with ID controller: " + groupId);
        service.deleteGroup(groupId);
    }
    @GetMapping("/{groupId}/members")
    public List<GroupMember> getMembers(@PathVariable String groupId) {
        return service.getMembers(groupId);
    }

}
