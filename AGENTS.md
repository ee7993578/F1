# Database target (user instruction, 2026-10-19)

- Use only SQL Server `localhost`, database `dpsbsr_edufox_net`, Windows Integrated Authentication for development and database tests.
- The user has authorized read/insert/update/delete on this local restored database. Keep tests isolated and remove only records created by the test.
- Do not connect to the old remote `beta_dpsbsr_edufox_net` database, including the connection in `Old/Web.config`. Do not use that configuration to start the backend.
- Start the backend with `backend/start-local.ps1`. It pins the local datasource and enables write operations. Do not call legacy stored procedures; port their logic to controller/service/repository code.
