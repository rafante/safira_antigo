<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
    <link rel="shortcut icon" href="${resource(dir: 'img', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'login.css')}"/>
    <script src="${resource(dir: 'js', file: 'login.js')}"></script>

</head>

<%
    random = new Random();
    def bgName = 'bg' + random.nextInt(22) + '.jpg';
%>
<body onload="document.getElementById('username').select();">

<form class="form-login" action='${postUrl}' method='POST'>
    <p class="field" action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
        <input type="text" id="username" name="j_username" placeholder="Login">
        <i class="icon-user icon-large"></i>
    </p>

    <p class="field">
        <input type="password" id="password" name="j_password" placeholder="Senha">
        <i class="icon-lock icon-large"></i>
    </p>

    <p class="submit">
        <button type="submit" name="submit"><i class="icon-arrow-right icon-large"></i></button>
    </p>

    <div style="text-align: center;padding-top: 8px">
        <img src="${resource(dir: 'img', file: 'logo.png')}">

        <g:if test='${flash.message}'>
            <div class='login_message'>${flash.message}</div>
        </g:if>
    </div>
</form>

</body>
</html>