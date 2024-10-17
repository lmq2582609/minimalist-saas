<template>
    <div class="w-[100%]" style="height: calc(100% - 50px)" v-if="hasPerm(['system:swagger'])">
        <iframe class="w-[100%] h-[100%]" :src="swaggerUrl"></iframe>
    </div>
    <div class="w-[100%] flex items-center" style="height: calc(100% - 50px)" v-else>
        <a-empty>
            <template #image>
                <icon-close-circle-fill />
            </template>
            <span>暂无访问权限，请联系管理员分配权限</span>
        </a-empty>
    </div>
</template>
<script setup>
import {ref} from "vue";
import {hasPerm} from "~/utils/sys.js";

const swaggerUrl = ref(import.meta.env.VITE_API_BASE_PREFIX + '/doc.html')
</script>
<style scoped></style>
