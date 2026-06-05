package br.ufscar.dc.dsw.sistema_pescd.domain;

public enum StatusAluno {
    NAO_ENVIADO("não enviado"),
    PLANO_ENVIADO("plano enviado"),
    PLANO_APROVADO("plano aprovado"),
    DOCUMENTACAO_ENVIADA("documentação enviada"),
    RELATORIO_ENVIADO("relatório enviado"),
    RELATORIO_APROVADO_SUPERVISOR("relatório aprovado pelo supervisor"),
    CONCLUIDO_RESPONSAVEL("concluído pelo responsável");

    private final String descricao;

    StatusAluno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}