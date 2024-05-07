<template>
  <div class="script-container">
    <span>SCRIPT:</span>
    <Textarea v-model="editor"></Textarea>
  </div>
  <Button label="RUN" @click="run()" />
  <div v-if="output" class="output-container">
    <span>OUTPUT:</span>
    {{ output }}
  </div>
</template>
<script lang="ts">
  
import { defineComponent } from "vue";
import Button from "primevue/button";
import Textarea from "primevue/textarea";
import Editor from "primevue/editor";
import { QS } from "@/https";

export default defineComponent({
  name: "QueryProcessor",
  components: {
    Button,
    Editor,
    Textarea
  },
  data() {
    return {
      editor: '',
      output: ''
    }
  },
  methods: {
    handleCodeEditor(editor: any) {
      editor.format('Code', true)
    },
    run() {
      this.output = 'oioioioi';
      QS.post('/processor', JSON.stringify(this.editor)).then(res => {
        console.log(res);
      });
    },
  }
});

</script>

<style scoped>
textarea {
  background-color: black;
  color: rgb(0, 145, 255);
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  width: 1240px !important;
  min-height: 360px;
  border: none;

}
.script-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin: 12px 0;
}
.output-container {
  display: flex;
  flex-direction: column;
  margin: 12px 0;
}
</style>

