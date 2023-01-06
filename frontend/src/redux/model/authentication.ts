import { createModel } from "@rematch/core";
import { RootModel } from "../models";
import {
  LoginRequest,
  LoginResponse,
  MessageResponse,
  UserIdentityAvailability,
} from "../../client/movies/generator-output";
import { authApi } from "../../client/movies/MoviesApi";
import { AxiosResponse } from "axios";
import jwt_decode, { JwtPayload } from "jwt-decode";

interface MyJwtPayload extends JwtPayload {
  roles: string;
}

export type State = {
  showCaseValue: Array<String>;
  isLoading: boolean;
  loaded: boolean;
  isEmailAvailable: boolean;
  isUsernameAvailable: boolean;
  isAuthenticated: boolean;
};

export const authentication = createModel<RootModel>()({
  state: {
    showCaseValue: [],
    isLoading: false,
    loaded: false,
    isEmailAvailable: false,
    isUsernameAvailable: false,
    isAuthenticated: false,
  } as State,
  reducers: {
    startLoading: (state) =>
      reduce(state, {
        isLoading: true,
      }),
    stopLoading: (state) =>
      reduce(state, {
        isLoading: false,
        loaded: true,
      }),
    isEmailAvailable: (state, payload: boolean) =>
      reduce(state, {
        isEmailAvailable: payload,
      }),
    isUsernameAvailable: (state, payload: boolean) =>
      reduce(state, {
        isUsernameAvailable: payload,
      }),
    setIsAuthenticated: (state, payload: boolean) =>
      reduce(state, {
        isAuthenticated: payload,
      }),
  },
  effects: (dispatch) => ({
    async checkEmailAvailability(email: string) {
      authApi
        .checkEmailAvailability(email)
        .then((response: AxiosResponse<UserIdentityAvailability>) => {
          if (response.status === 200 && response.data !== null) {
            dispatch.isEmailAvailable(response.data.isAvailable);
          }
        })
        .catch((reason: any) => {
          // log
          // notification
          console.log(reason);
        });
    },
    async checkUsernameAvailability(username: string) {
      authApi
        .checkUsernameAvailability(username)
        .then((response: AxiosResponse<UserIdentityAvailability>) => {
          if (response.status === 200 && response.data !== null) {
            dispatch.isUsernameAvailable(response.data.isAvailable);
          }
        })
        .catch((reason: any) => {
          // log
          // notification
          console.log(reason);
        });
    },
    async registerAccount({ registrationRequest, options }) {
      authApi
        .registerAccount(registrationRequest, options)
        .then((response: AxiosResponse<MessageResponse>) => {
          if (response.status === 200 && response.data !== null) {
            //notification ?
            console.log(response.data.message);
          }
        })
        .catch((reason: any) => {
          // log
          // notification
          console.log(reason);
        });
    },
    async authenticateAccount(loginRequest: LoginRequest) {
      authApi
        .authenticateAccount(loginRequest)
        .then((response: AxiosResponse<LoginResponse>) => {
          if (
            response.status === 200 &&
            response.data !== null &&
            response.data.accessToken !== null &&
            response.data.accessToken !== undefined
          ) {
            window.localStorage.setItem("jwtToken", response.data.accessToken);
            let decoded = jwt_decode<MyJwtPayload>(response.data.accessToken);
            window.localStorage.setItem("rolesFromJwt", decoded.roles);
            if (decoded.exp !== undefined) {
              window.localStorage.setItem(
                "jwtExpiresAt",
                decoded.exp.toString()
              );
            }
            dispatch.authentication.setIsAuthenticated(true);
            dispatch.account.getCurrentAccount();
          }
          if (response.status === 401) {
            console.log("401: Bad Credentials");
          }
        })
        .catch((reason: any) => {
          // log
          // notification
          console.log(reason);
        });
    },
  }),
});

export const reduce = <T>(s: T, v: any) => Object.assign({}, s, v);
