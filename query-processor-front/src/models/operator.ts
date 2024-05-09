export class Operator {
    father: null
    children: any[]
    params: any
    operation: any
    constructor (params: any[], operation: string){
        this.father = null
        this.children = []
        this.params = params
        this.operation = operation
    }
}