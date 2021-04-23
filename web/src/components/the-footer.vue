<template>
    <a-layout-footer style="text-align: center">
        <span v-show="user.id">{{user.name}}の知识库</span>
        <span v-show="!user.id">个人知识库系统</span>
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