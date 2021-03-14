package com.github.bestheroz.demo.api.auth;

import com.github.bestheroz.demo.api.entity.member.TableMemberEntity;
import com.github.bestheroz.demo.api.entity.member.TableMemberRepository;
import com.github.bestheroz.standard.common.authenticate.JwtTokenProvider;
import com.github.bestheroz.standard.common.exception.BusinessException;
import com.github.bestheroz.standard.common.response.ApiResult;
import com.github.bestheroz.standard.common.response.Result;
import com.github.bestheroz.standard.common.util.AuthenticationUtils;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {
  @Resource private AuthService authService;
  @Resource private TableMemberRepository tableMemberRepository;

  @PostMapping(value = "/login")
  @ResponseBody
  ResponseEntity<ApiResult> login(@RequestBody final TableMemberEntity payload) {
    return Result.ok(this.authService.login(payload.getId(), payload.getPassword()));
  }

  @GetMapping(value = "/me")
  public ResponseEntity<ApiResult> getMine(
      @RequestHeader(value = "Authorization") final String token) {
    return Result.ok(
        this.tableMemberRepository
            .findById(JwtTokenProvider.getUserPk(token))
            .orElseThrow(() -> BusinessException.FAIL_TRY_LOGIN_FIRST));
  }

  @GetMapping(value = "/refreshToken")
  public ResponseEntity<ApiResult> refreshToken(
      @RequestHeader(value = "AuthorizationR") final String refreshToken) {
    return Result.ok(this.authService.getNewAccessToken(refreshToken));
  }

  @PostMapping(value = "/initPassword")
  ResponseEntity<ApiResult> initPassword(@RequestBody final TableMemberEntity payload) {
    return Result.ok(this.authService.initPassword(payload.getId(), payload.getPassword()));
  }

  @DeleteMapping(value = "/logout")
  public void logout() {
    this.authService.logout();
    AuthenticationUtils.logout();
  }
}
