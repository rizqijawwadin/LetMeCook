import express from "express";
import dotenv from "dotenv";
import router from "./routes/index.js";
import sequelize from "./config/database.js";

dotenv.config();

const app = express();

app.use(express.json());
app.use(router);

sequelize
  .sync()
  .then(() => {
    console.log("Database connected successfully");
  })
  .catch((error) => {
    console.error("Database connection failed:", error);
  });

const PORT = process.env.APP_PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port http://localhost:${PORT}`);
});
