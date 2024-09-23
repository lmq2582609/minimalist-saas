<template>
    <div class="w-[100%]" style="height: calc(100% - 50px)">
        <iframe class="w-[100%] h-[100%]" :src="swaggerUrl"></iframe>
    </div>
</template>
<script setup>
import {ref} from "vue";

const swaggerUrl = ref(import.meta.env.VITE_API_BASE_PREFIX + '/doc.html')
</script>
<style scoped></style>
