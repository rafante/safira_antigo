modules = {
    login {
        dependsOn 'core-css-img'

        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'login.css']

        dependsOn 'core-js'
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'js', file: 'login.js']
    }

    forms {
        dependsOn 'core-css-img', 'font-awesome'
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'bootstrap.min.css']
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'bootstrap-glyphicons.css']
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'icheck/flat/blue.css']
//        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'select2.css']
//        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'select2-bootstrap.css']
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'unicorn.main.css']
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'unicorn.blue.css']
        //        resource disposition: 'head', url:[dir:'css', file:'unicorn.grey.css']
        //        resource disposition: 'head', url:[dir:'css', file:'unicorn.red.css']
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'asgard.css']
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'jquery.gritter.css']
//        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'nprogress.css']

        dependsOn 'core-js'
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.browser.min.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.price_format.1.8.min.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.meio.mask.min.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.jpanelmenu.min.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.icheck.min.js']
//        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'select2.min.js']
//        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'select2_locale_pt-BR.js']

        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'bootstrap.min.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'unicorn.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.pjax.js']
//        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'nprogress.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.utils.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.autocomplete.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.select.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.date.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.filter.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.editable.grid.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'asgard.grid.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', plugin:'asgard-base', dir: 'js', file: 'application.js']
    }

    'core-css-img' {
        resource disposition: 'head', url: [plugin:'asgard-base', dir: 'css', file: 'jquery-ui.min.css']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'img', file: 'favicon.ico', attrs: [type: "image/x-icon"]]
    }

    'core-js' {
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery.min.js']
        resource disposition: 'defer', url: [plugin:'asgard-base', dir: 'js', file: 'jquery-ui.custom.min.js']
    }
}