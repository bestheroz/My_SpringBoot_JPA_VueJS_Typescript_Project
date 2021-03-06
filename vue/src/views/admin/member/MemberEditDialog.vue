<template>
  <div>
    <v-dialog v-model="syncedDialog" max-width="100%" width="60vw">
      <v-card>
        <dialog-title :is-new="isNew" prefix="사용자">
          <template #buttons>
            <button-icon-tooltip
              icon="mdi-window-close"
              text="닫기"
              @click="syncedDialog = false"
              top
            />
          </template>
        </dialog-title>
        <v-card-text>
          <ValidationObserver ref="observer">
            <v-row dense>
              <v-col cols="12" md="4">
                <ValidationProvider
                  v-slot="{ errors }"
                  name="사용자아이디"
                  rules="required|max:50"
                >
                  <v-text-field
                    v-model="vModel.userId"
                    label="*사용자아이디"
                    :counter="50"
                    :error-messages="errors"
                    :disabled="!isNew"
                  />
                </ValidationProvider>
              </v-col>
              <v-col cols="12" md="4">
                <ValidationProvider
                  v-slot="{ errors }"
                  name="사용자명"
                  rules="required|max:100"
                >
                  <v-text-field
                    v-model="vModel.name"
                    label="*사용자명"
                    :counter="100"
                    :error-messages="errors"
                  />
                </ValidationProvider>
              </v-col>
              <v-col cols="12" md="4">
                <v-switch
                  v-model="vModel.available"
                  :label="vModel.available | getSwitchLabel"
                  inset
                />
              </v-col>
              <v-col cols="12" md="4">
                <ValidationProvider
                  v-slot="{ errors }"
                  name="권한"
                  rules="required"
                >
                  <v-select
                    v-if="AUTHORITY"
                    v-model.number="vModel.authorityId"
                    :items="
                      AUTHORITY.map((code) => {
                        return { value: parseInt(code.value), text: code.text };
                      })
                    "
                    label="*권한"
                    :error-messages="errors"
                  />
                </ValidationProvider>
              </v-col>
              <v-col cols="12" md="4">
                <datetime-picker
                  v-model="vModel.expired"
                  label="만료일"
                  full-width
                />
              </v-col>
              <v-col cols="12" md="4">
                <ValidationProvider
                  v-if="isNew"
                  v-slot="{ errors }"
                  name="비밀번호"
                  vid="password"
                  rules="max:20"
                >
                  <v-text-field
                    v-model="vModel.password"
                    label="비밀번호"
                    :counter="20"
                    :error-messages="errors"
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    @click:append="show1 = !show1"
                  />
                </ValidationProvider>
              </v-col>
              <v-col cols="12" md="4">
                <ValidationProvider
                  v-if="isNew"
                  v-slot="{ errors }"
                  name="비밀번호 확인"
                  rules="required|confirmed:password|max:20"
                >
                  <v-text-field
                    v-model="password2"
                    label="*비밀번호 확인"
                    :counter="20"
                    :error-messages="errors"
                    :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show2 ? 'text' : 'password'"
                    clearable
                    @click:append="show2 = !show2"
                  />
                </ValidationProvider>
                <v-btn
                  v-if="!isNew"
                  color="warning"
                  outlined
                  @click="resetPassword"
                >
                  비밀번호 초기화
                </v-btn>
              </v-col>
            </v-row>
          </ValidationObserver>
        </v-card-text>
        <created-updated-bar
          :created-date-time="vModel.created"
          :updated-date-time="vModel.updated"
          v-if="vModel.created || vModel.updated"
        />
        <dialog-action-button
          :loading="loading"
          @click:save="save"
          @click:close="syncedDialog = false"
        />
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, PropSync, Ref, VModel, Vue } from "vue-property-decorator";
import type { SelectItem } from "@/common/types";
import { getApi, patchApi, postApi } from "@/utils/apis";
import DatetimePicker from "@/components/picker/DatetimePicker.vue";
import { ValidationObserver } from "vee-validate";
import pbkdf2 from "pbkdf2";
import ButtonIconTooltip from "@/components/button/ButtonIconTooltip.vue";
import DialogTitle from "@/components/title/DialogTitle.vue";
import DialogActionButton from "@/components/button/DialogActionButton.vue";
import type { Member } from "@/common/models";
import CreatedUpdatedBar from "@/components/history/CreatedUpdatedBar.vue";

@Component({
  name: "MemberEditDialog",
  components: {
    CreatedUpdatedBar,
    DialogActionButton,
    DialogTitle,
    ButtonIconTooltip,
    DatetimePicker,
  },
})
export default class extends Vue {
  @VModel({ required: true }) vModel!: Member;
  @PropSync("dialog", { required: true, type: Boolean }) syncedDialog!: boolean;
  @Ref("observer") readonly observer!: InstanceType<typeof ValidationObserver>;

  loading = false;
  AUTHORITY: SelectItem<number>[] = [];
  password2 = "";
  show1 = false;
  show2 = false;

  protected async created(): Promise<void> {
    const response = await getApi<SelectItem<number>[]>("auth/codes");
    this.AUTHORITY = response.data || [];
  }

  get isNew(): boolean {
    return !this.vModel.id;
  }

  protected async save(): Promise<void> {
    const isValid = await this.observer.validate();
    if (!isValid) {
      return;
    }
    this.isNew ? await this.create() : await this.update();
  }

  protected async create(): Promise<void> {
    this.loading = true;
    const params = { ...this.vModel };
    if (params.password) {
      params.password = pbkdf2
        .pbkdf2Sync(params.password, "salt", 1, 32, "sha512")
        .toString();
    }
    const response = await postApi<Member>("admin/members/", params);
    this.loading = false;
    if (response?.code?.startsWith("S")) {
      await this.$store.dispatch("initMemberCodes");
      this.syncedDialog = false;
      this.$emit("created", response.data);
    }
  }

  protected async update(): Promise<void> {
    this.loading = true;
    const params = { ...this.vModel };
    if (params.password) {
      params.password = pbkdf2
        .pbkdf2Sync(params.password, "salt", 1, 32, "sha512")
        .toString();
    }
    const response = await patchApi<Member>(
      `admin/members/${this.vModel.id}/`,
      params,
    );
    this.loading = false;
    if (response?.code?.startsWith("S")) {
      if (this.vModel.id === this.$store.getters.user.id) {
        await this.$store.dispatch("reissueAccessToken");
      }
      await this.$store.dispatch("initMemberCodes");
      this.syncedDialog = false;
      this.$emit("updated", response.data);
    }
  }

  protected async resetPassword(): Promise<void> {
    this.loading = true;
    await postApi<Member>(
      `admin/members/${this.vModel.id}/resetPassword`,
      this.vModel,
    );
    this.loading = false;
  }
}
</script>
