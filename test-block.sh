#!/bin/bash
# Run only BlockServiceTest and BlockControllerTest
mvn -Dtest=com.programming.techie.springredditclone.service.BlockServiceTest,com.programming.techie.springredditclone.controller.BlockControllerTest test 