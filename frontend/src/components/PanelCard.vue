<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  extra: {
    type: String,
    default: ""
  }
});
</script>

<template>
  <section class="panel-card">
    <header class="panel-card__header">
      <h3>{{ title }}</h3>
      <button v-if="extra" class="panel-card__action" type="button">{{ extra }}</button>
    </header>
    <div class="panel-card__body">
      <slot />
    </div>
  </section>
</template>
