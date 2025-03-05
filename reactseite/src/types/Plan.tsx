export type Plan = {
    id :number,
    benutzer : number,
    projekt : number,
    stunde :number,
    planvon :string,
    planbis :string,
    erstelltVon: number,
    aktualisiertVon:number
    erstelltAm :string,
    aktualisiertAm :string
  }

  export type PostPlan ={
    benutzer : number,
    projekt : number,
    stunde :number,
    planvon :string,
    planbis :string,
    erstelltVon: number,
    aktualisiertVon:number
    erstelltAm :string,
    aktualisiertAm :string
  }