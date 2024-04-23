import './assets/main.css'

import { createApp } from 'vue'
import PrimeVue from 'primevue/config';
import App from './App.vue'
import router from './router'
import Button from 'primevue/button';
import Editor from 'primevue/editor';
import Textarea from 'primevue/textarea';


import 'primevue/resources/themes/aura-light-green/theme.css'

const app = createApp(App)

app.use(router)
.use(PrimeVue)
.component("Button", Button)
.component("Editor", Editor)
.component("Textarea", Textarea);

app.mount('#app')
