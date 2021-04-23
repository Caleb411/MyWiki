<template>
    <a-layout-footer style="text-align: center">
      <span v-show="!user.id">陈霖の知识库</span>
      <span v-show="user.id">当前管理员：{{user.name}}</span>
    </a-layout-footer>
</template>

<script lang="ts">
import {computed, defineComponent} from 'vue';
import store from "@/store";

    export default defineComponent({
      name: 'the-footer',
      setup() {
        const user = computed(() => store.state.user);
        return {
          user
        }
      }
    });
</script>