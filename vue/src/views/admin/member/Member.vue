<template>
  <div>
    <v-row no-gutters>
      <v-col cols="12">
        <member-list />
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import MemberList from "@/views/admin/member/MemberList.vue";

@Component({
  name: "Member",
  components: { MemberList },
})
export default class extends Vue {}
</script>
