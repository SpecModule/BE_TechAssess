package com.example.sourcebase.controller;

import com.example.sourcebase.domain.User;
import com.example.sourcebase.domain.dto.reqdto.user.RegisterReqDTO;
import com.example.sourcebase.domain.dto.resdto.user.UserResDTO;
import com.example.sourcebase.mapper.RegisterMapper;
import com.example.sourcebase.mapper.UserMapper;
import com.example.sourcebase.service.IUserService;
import com.example.sourcebase.util.ErrorCode;
import com.example.sourcebase.util.ResponseData;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.example.sourcebase.util.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("api/users")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserRestController {
    IUserService userService;
    RegisterMapper registerMapper;
    @GetMapping()
    public ResponseEntity<ResponseData<?>> getAllUser() {
        return ResponseEntity.ok(
                ResponseData.builder()
                        .code(SuccessCode.GET_SUCCESSFUL.getCode())
                        .message(SuccessCode.GET_SUCCESSFUL.getMessage())
                        .data(userService.getAllUser())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<?>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseData.builder()
                        .code(SuccessCode.GET_SUCCESSFUL.getCode())
                        .message(SuccessCode.GET_SUCCESSFUL.getMessage())
                        .data(userService.getUserById(id))
                        .build());
    }
    @GetMapping("/current-user/{username}")
    public ResponseEntity<ResponseData<?>> getCurrentUser(@PathVariable String username) {
        return ResponseEntity.ok(
                ResponseData.builder()
                        .code(SuccessCode.GET_SUCCESSFUL.getCode())
                        .message(SuccessCode.GET_SUCCESSFUL.getMessage())
                        .data(userService.getUserDetailBy(username))
                        .build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<?>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ResponseData.builder()
                        .code(SuccessCode.DELETE_SUCCESSFUL.getCode())
                        .message(SuccessCode.DELETE_SUCCESSFUL.getMessage())
                        .build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<?>> updateUser(@PathVariable Long id, @RequestParam(value = "user", required = false) RegisterReqDTO request, @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws IOException {
        RegisterReqDTO registerReqDTO = registerMapper.readValue(String.valueOf(request), RegisterReqDTO.class);
        return ResponseEntity.ok(
                ResponseData.builder()
                        .code(SuccessCode.UPDATE_SUCCESSFUL.getCode())
                        .message(SuccessCode.UPDATE_SUCCESSFUL.getMessage())
                        .data(userService.updateUser(id, registerReqDTO, avatar))
                        .build());
    }

    @GetMapping("/{userId}/same-project")
    public ResponseEntity<ResponseData<?>> getAllUserHadSameProject(@PathVariable Long userId){
        List<UserResDTO> usersHadSameProject = userService.getAllUserHadSameProject(userId);

        if(usersHadSameProject.isEmpty()) {
            return ResponseEntity.status(ErrorCode.USER_NOT_FOUND.getHttpStatus()).body(
                    ResponseData.builder()
                            .code(ErrorCode.USER_NOT_FOUND.getCode())
                            .message(ErrorCode.USER_NOT_FOUND.getMessage())
                            .build()
            );
        }

        return ResponseEntity.ok(
                ResponseData.builder()
                        .code(SuccessCode.GET_SUCCESSFUL.getCode())
                        .message(SuccessCode.GET_SUCCESSFUL.getMessage())
                        .data(usersHadSameProject)
                        .build()
        );
    }

}
