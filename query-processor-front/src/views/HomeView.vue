<template>
  <div class="script-container">
    <span>SCRIPT:</span>
    <Textarea v-model="editor"></Textarea>
  </div>
  <Button label="RUN" @click="onSubmit()" />
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
import { Operator } from "@/models/operator";
import { QS } from "@/https";

export default defineComponent({
  name: "QueryProcessor",
  components: {
    Button,
    Editor,
    Textarea
  },
  setup() {
    const keywords = [
      "join", 
      "where", 
      "select", 
      ";"
    ];
    const tablesNames = [
      "usuario", 
      "contas", 
      "tipoconta", 
      "movimentacao", 
      "tipomovimento", 
      "categoria"
    ]
    const operators = [
      "=", 
      ">", 
      "<", 
      "<=", 
      ">=", 
      "<>"
    ];
    const atributosTabelas = [
      "idusuario", 
      "nome", 
      "logradouro", 
      "numero", 
      "bairro", 
      "cep", 
      "UF", 
      "datanascimento",
      "idcontas",
      "Logradouro", 
      "descricao", 
      "tipoconta_idtipoconta", 
      "usuario_idusuario", 
      "saldoinicial",
      "idtipoConta", 
      "descricao",
      "usuario_idusuario",
      "tipoconta_idtipoconta",
      "idmovimentacao", 
      "datamovimentacao", 
      "descricao", 
      "tipomovimento_idtipomovimento", 
      "categoria_idcategoria", 
      "contas_idcontas", 
      "valor",
      "idtipomovimento", 
      "descmovimentacao",
      "idtipoConta",
      "idcategoria", 
      "desccategoria", 
      "Descrição",
      "uf",
      "descrição"
    ];
    return {
      keywords,
      tablesNames,
      operators,
      atributosTabelas
    }
  },
  data() {
    return {
      usuario: [] as any[],
      editor: '' as string,
      output: '' as string
    }
  },
  methods: {

    onSubmit(){
        this.parse(this.editor)
    },

    parse(command: any){
        let s = command.replaceAll(',', '');
        s = s.split(" ")

        let select = this.parseProjection(s.toUpperCase())
        let joins = this.parseJoin(s)
        let where = this.parseSelection(s)
        this.graph(select, joins, where)
    },

    graph(select: any, join: any, where: any){
        let nodeArray = [] as any[];
        let lastTable = select.tables[0];

        select.tables.forEach((t: any) => {
            nodeArray.push(new Operator(t, "TABLE"))
        })

        join.forEach((j: any) => {
            nodeArray.push(new Operator(j.table, "TABLE"))
        })

        if(nodeArray.length > 1 && join.length == 0){
            let produto = new Operator([], "PRODUTO CARTESIANO")
            nodeArray.forEach(c =>{
                produto.children.push(c)
                produto.params.push(c.params)
            })
            nodeArray.push(produto)
        }
        join.forEach((j: any) =>{
            let op = new Operator([j.table, lastTable], "JUNÇÃO")
            lastTable = j.table
            nodeArray.forEach(t => {
                if(op.params.includes( t.params))
                    op.children.push(t)
            })
            nodeArray.push(op)
        })

        if(where != null){
            let w = new Operator(where, "SELEÇÃO")
            w.children.push(nodeArray.slice(-1))
            nodeArray.push(w)
        }

        console.log("ordem de execução antes da aplicação de heurísticas 🤔")
        let p = new Operator(select.params, "PROJEÇÃO")
        p.children.push(nodeArray.slice(-1))
        nodeArray.push(p)
        nodeArray.forEach(node => {
            if(!(node.operation == 'TABLE'))
                console.log(`operação: ${node.operation} com: ${node.params}`)
        })

        console.log('\n' )
        console.log("\nordem após otimização priorizando projeções e seleções 🤓")

        for(let i=0; i<nodeArray.length; i++){
            let tt = []
            if(nodeArray[i].operation == 'SELEÇÃO' ||nodeArray[i].operation == 'PROJEÇÃO' ){
                for(let j =0; j< i; j++){
                    if(nodeArray[j].operation == "TABLE")
                        tt.push(nodeArray[j].params)
                }
                console.log(`operação: ${nodeArray[i].operation} em: ${tt}, com: ${nodeArray[i].params}`)
            }
        }
        for(let k=0; k<nodeArray.length; k++){
            if(nodeArray[k].operation == 'JUNÇÃO' || nodeArray[k].operation == 'PRODUTO CARTESIANO')
                console.log(`operação: ${nodeArray[k].operation} em: ${nodeArray[k].params}, com ${nodeArray[k].params}`)
            }

        console.log('\n GRAFO NÃO OTIMIZADO 🌞 ')
        console.log(nodeArray.pop())
    },

    parseProjection(command: any){
        let s = false
        let sParams = false
        let params = [] as any[]
        let subQuery = [] as any[]
        let checker = (arr: string | any[], target: any[]) => target.every(v => arr.includes(v) || arr.includes(v.toLowerCase) || arr.includes(v.toUpperCase));

        if(command.includes("SELECT")){
            command.forEach((e: any) => {
                if(e.toLowerCase() == "from"){
                    s = false
                    sParams = true
                }
                if(s)
                  params.push(e)
                else{
                    if(this.keywords.includes(e))
                        sParams = false
                    if(sParams)
                        subQuery.push(e)
                }
                if(e.toLowerCase() == "select")
                    s = true
            });
            subQuery.shift()
            const uppercasedParams = params.map(atributos => atributos.toLowerCase());

            if(checker(this.tablesNames, subQuery)){
                if(checker(this.atributosTabelas,uppercasedParams)){
                    return {params: params, tables: subQuery}  
                }else{
                    alert("Atributos da tabela estão invalidos");
                }
            }else{
                alert("Tabela não encontrada no banco de dadaos");
            }
        }else{
            alert("Comando Inválido")
        }
    },

    parseSelection(command: any){
        let s = false
        let expression = ""
        command.forEach((e: any)=>{
            if(s)
                expression += " " + e
            if(e.toLowerCase() == "where")
                s = true
        })
        let vetor_palavras = expression.split(" ")
        let incluso = false
        if(s){
            for(let i = 0 ; i < vetor_palavras.length; i++){
                if(this.operators.includes(vetor_palavras[i])){
                    incluso = true
                }
            }
            if(incluso){
                return expression
            }else{
                alert("Comando Inválido na parte do Where")
            }
        }
    },

    parseJoin(command: any){
        let s = false
        let joins = []
        let expression = ""
        for(let i = 0; i < command.length; i++){
            if(command[i].toLowerCase()  == "join"){
                joins.push({
                    table: command[i+1],
                    condition: `${command[i+3]} ${command[i+4]} ${command[i+5]}`
                })
            }
        }
        return joins
    }
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
: string | any[]: any[](: string)(: string): any[](: string): string | any[]: string | any[]: any[](: string)(: string): any[](: string): string | any[]
