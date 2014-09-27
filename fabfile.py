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
    run_server()

def update_code():
    with cd(PROJECT_DIR):
        run("git reset --hard HEAD")
        run("git pull --rebase origin master")

def kill_server():
    run("pkill java", warn_only=True)

def run_server():
    with cd(PROJECT_DIR):
        run("nohup lein run &")
