# -*- coding: utf-8 -*-

from fabric.api import run, cd, env
from fabric.decorators import task


env.user = 'cloudsigma'
env.hosts = ['31.171.245.192']


PROJECT_DIR = "/home/cloudsigma/thefinalcountapp"


@task
def deploy():
    update_code()
    kill_server()
    compile_cljs()
    run_server()

def update_code():
    with cd(PROJECT_DIR):
        run("git reset --hard HEAD")
        run("git pull --rebase origin master")

def compile_cljs():
    with cd(PROJECT_DIR):
        run("lein cljsbuild once")

def kill_server():
    run("pkill java", warn_only=True)

def run_server():
    with cd(PROJECT_DIR):
        run_bg("lein run")

def run_bg(cmd):
    run("dtach -n `mktemp -u /tmp/{socket}.XXXX` {cmd}".format(socket="dtach", cmd=cmd))
