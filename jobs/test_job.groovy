pipelineJob("Production IOS Sanity tests"){
  parameters {
    stringParam('ENVIRONMENT', 'PRODUCTION', 'where to deploy')
    stringParam('PLATFORM', 'IOS', 'where to deploy')
    stringParam('TYPE', 'SANITY', 'where to deploy')
  }
  definition {
    cps {
      sandbox(true)
      script('''
        testFunction(params.ENVIRONMENT, params.PLATFORM, params.TYPE)
      '''.stripIndent())
     }
   }
}

