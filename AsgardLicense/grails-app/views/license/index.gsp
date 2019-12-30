<html>
<head>
    <title>Licença</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="body">
    <h1>Detalhes da Licença</h1>

    <g:form enctype="multipart/form-data" method="post">
        Chave de Ativação:<br/>
        <% out << License.generateActivationCode() %>
        <br/>
        <br/>

        Serial salvo:
        <br/>
        <% out << license?.serial %>
        <br/>
        <br/>
        <label for="serial">Ativar novo serial:</label>
        <input type="text" id="serial" name="serial" value="${license?.serial}">
        <input type="submit" value="Ativar">
    </g:form>

    <div>
        <g:if test="${!license?.validateSerial()}">
            <font color="red">Serial inválido ou expirado!</font>
        </g:if>
        <g:else>
            <font color="green">Serial validado com sucesso! Valido até: <% out << License.validade?.format("dd/MM/yyyy") %></font><br />
            <g:link controller="index">Acessar sistema</g:link>
        </g:else>
    </div>
</div>
</body>
</html>