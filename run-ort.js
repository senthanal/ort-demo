import { spawnSync } from "node:child_process";
import fs from "node:fs";
import { resolve } from "node:path";
import process from "node:process";

runORT();

function runORT() {
  // Remove and recreate the results output directory
  if(fs.existsSync(resolve(process.cwd(), "results"))){
    fs.rmSync(resolve(process.cwd(), "results"), { recursive: true, force: true });
    fs.mkdirSync(resolve(process.cwd(), "results"));
  }
  cmd("docker build -t ort-demo . --no-cache");
  cmd(`docker run -v ${process.cwd()}/results:/home/ort/results:rw ort-demo --no-cache`);
  cleanUpDocker();
}

function cleanUpDocker() {
  cmd("docker stop $(docker ps -a -q --filter ancestor=ort-demo)");
  cmd("docker rm $(docker ps -a -q --filter ancestor=ort-demo)");
  cmd("docker rmi ort-demo");
}

function cmd(...command) {
  let {stdout, stderr} = spawnSync(command[0],{
    shell: true,
    stdio: "inherit",
    cwd: process.cwd(),
    encoding: "utf-8",
    env: process.env
  });
  if(stdout)
    process.stdout.write(x.toString());
  if(stderr)
    process.stderr.write(x.toString());
}
