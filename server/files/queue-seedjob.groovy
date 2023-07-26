import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

println 'Queuing seed-job at Jenkins startup ...'
def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))
new DslScriptLoader(jobManagement).runScript('queue("Seed or update all pipelines")')

